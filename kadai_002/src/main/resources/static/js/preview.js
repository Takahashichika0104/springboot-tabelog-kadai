const imageInput = document.getElementById('image');
const imagePreview = document.getElementById('imagePreview');

imageInput.addEventListener('change', () => {
  if (imageInput.files[0]) {
    const fileReader = new FileReader();
    fileReader.onload = () => {
      imagePreview.innerHTML = `<img src="${fileReader.result}">`;
    };
    fileReader.readAsDataURL(imageInput.files[0]);
  } else {
    imagePreview.innerHTML = '';
  }
});
