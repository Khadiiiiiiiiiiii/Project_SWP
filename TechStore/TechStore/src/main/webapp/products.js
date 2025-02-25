document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".product-carousel-container").forEach((container) => {
        const carousel = container.querySelector(".product-carousel");
        const leftBtn = container.querySelector(".left-btn");
        const rightBtn = container.querySelector(".right-btn");

        let scrollAmount = 0;
        const scrollStep = 220;

        rightBtn.addEventListener("click", function () {
            carousel.scrollTo({
                left: (scrollAmount += scrollStep),
                behavior: "smooth",
            });
        });

        leftBtn.addEventListener("click", function () {
            carousel.scrollTo({
                left: (scrollAmount -= scrollStep),
                behavior: "smooth",
            });
        });
    });
});
