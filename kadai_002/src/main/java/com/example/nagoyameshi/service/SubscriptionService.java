package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.CreditCard;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.CreditCardRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.product-id}")
    private String stripeProductId;

    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;

    public SubscriptionService(UserRepository userRepository, CreditCardRepository creditCardRepository) {
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
    }

    //Stripe Checkoutセッションを作成し、リダイレクト先URLを返す。
    public String createCheckoutSession(User user, String baseUrl) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        // 商品IDからアクティブな価格を取得
        PriceListParams priceListParams = PriceListParams.builder()
                .setProduct(stripeProductId)
                .setActive(true)
                .build();
        PriceCollection prices = Price.list(priceListParams);

        if (prices.getData().isEmpty()) {
            throw new RuntimeException("商品に有効な価格が設定されていません。");
        }
        String priceId = prices.getData().get(0).getId();

        // Checkoutセッションを作成
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build())
                .setSuccessUrl(baseUrl + "/membership/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/membership");

        // 既存のStripe顧客IDがある場合はそれを使用
        if (user.getStripeCustomerId() != null) {
            builder.setCustomer(user.getStripeCustomerId());
        } else {
            builder.setCustomerEmail(user.getEmail());
        }

        Session session = Session.create(builder.build());
        return session.getUrl();
    }

    //決済完了後にユーザー情報とクレジットカード情報を更新する。
    @Transactional
    public void handleCheckoutSuccess(String sessionId, User user) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        Session session = Session.retrieve(sessionId);

        // 会員種別とStripe IDを更新
        user.setMembershipType("PREMIUM");
        user.setStripeCustomerId(session.getCustomer());
        user.setStripeSubscriptionId(session.getSubscription());
        userRepository.save(user);

        // 顧客に紐づくカード情報を取得
        PaymentMethodListParams pmListParams = PaymentMethodListParams.builder()
                .setCustomer(session.getCustomer())
                .setType(PaymentMethodListParams.Type.CARD)
                .build();
        PaymentMethodCollection paymentMethods = PaymentMethod.list(pmListParams);

        if (!paymentMethods.getData().isEmpty()) {
            PaymentMethod pm = paymentMethods.getData().get(0);
            upsertCreditCard(user, pm);
        }
    }

    // Billing Portal セッションを作成
    public String createBillingPortalSession(User user, String baseUrl) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        if (user.getStripeCustomerId() == null || user.getStripeCustomerId().isBlank()) {
            throw new IllegalStateException("Stripe顧客情報が見つかりません。先に有料会員登録を行ってください。");
        }

        com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams
            .builder()
                .setCustomer(user.getStripeCustomerId())
                .setReturnUrl(baseUrl + "/credit-card/edit/refresh")
                .build();

        com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);
        return session.getUrl();
    }

    // Stripe上の最新カード情報をDBに同期
    @Transactional
    public void syncLatestCard(User user) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        if (user.getStripeCustomerId() == null || user.getStripeCustomerId().isBlank()) {
            throw new IllegalStateException("Stripe顧客情報が見つかりません。先に有料会員登録を行ってください。");
        }

        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(user.getStripeCustomerId())
                .setType(PaymentMethodListParams.Type.CARD)
                .build();
        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);

        if (paymentMethods.getData().isEmpty()) {
            throw new IllegalStateException("登録されているクレジットカードが見つかりません。");
        }

        PaymentMethod latest = paymentMethods.getData().get(0);
        upsertCreditCard(user, latest);
    }

    // Stripeサブスクリプションを解約し、アプリ側の会員状態を更新
    @Transactional
    public void cancelSubscription(User user) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("ユーザーが見つかりません。"));

        if (managedUser.getStripeSubscriptionId() == null || managedUser.getStripeSubscriptionId().isBlank()) {
            throw new IllegalStateException("解約対象のサブスクリプション情報が見つかりません。");
        }

        boolean canceledOnStripe = false;

        try {
            Subscription subscription = Subscription.retrieve(managedUser.getStripeSubscriptionId());
            subscription.cancel();
            canceledOnStripe = true;
        } catch (StripeException e) {
            // DB上のsubscription_idが古い場合は、customerから有効サブスクを再探索して解約する
            if (!"resource_missing".equals(e.getCode())) {
                throw e;
            }
        }

        if (!canceledOnStripe && managedUser.getStripeCustomerId() != null && !managedUser.getStripeCustomerId().isBlank()) {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", managedUser.getStripeCustomerId());
            params.put("status", "active");
            params.put("limit", 1L);

            SubscriptionCollection subscriptions = Subscription.list(params);
            if (!subscriptions.getData().isEmpty()) {
                subscriptions.getData().get(0).cancel();
            }
        }

        // 双方向関連を先に外してから削除し、flush時のTransientObjectExceptionを回避
        creditCardRepository.findByUser(managedUser).ifPresent(card -> {
            managedUser.setCreditCard(null);
            card.setUser(null);
            creditCardRepository.delete(card);
        });

        managedUser.setMembershipType("FREE");
        managedUser.setStripeSubscriptionId(null);
        userRepository.save(managedUser);

    }

    private void upsertCreditCard(User user, PaymentMethod pm) {
        PaymentMethod.Card card = pm.getCard();

        String cardHolderName = (pm.getBillingDetails() != null && pm.getBillingDetails().getName() != null)
                ? pm.getBillingDetails().getName()
                : user.getName();

        CreditCard creditCard = creditCardRepository.findByUser(user).orElse(new CreditCard());
        creditCard.setUser(user);
        creditCard.setCardNumber("**** **** **** " + card.getLast4());
        creditCard.setCardHolderName(cardHolderName);
        creditCard.setExpirationDate(card.getExpMonth() + "/" + card.getExpYear());
        creditCard.setBrand(card.getBrand());
        creditCardRepository.save(creditCard);
    }
}
