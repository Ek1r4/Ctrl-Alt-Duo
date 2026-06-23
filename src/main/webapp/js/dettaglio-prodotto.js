document.addEventListener('DOMContentLoaded', function() {
    const btnMinus = document.getElementById('btn-minus');
    const btnPlus = document.getElementById('btn-plus');
    const inputQty = document.getElementById('qty-input');

    if (btnMinus && btnPlus && inputQty) {
        btnMinus.addEventListener('click', () => {
            let currentValue = parseInt(inputQty.value);
            if (currentValue > 1) {
                inputQty.value = currentValue - 1;
            }
        });

        btnPlus.addEventListener('click', () => {
            let currentValue = parseInt(inputQty.value);
            if (currentValue < 10) { // Limite massimo impostato a 10 pezzi
                inputQty.value = currentValue + 1;
            }
        });
    }
});