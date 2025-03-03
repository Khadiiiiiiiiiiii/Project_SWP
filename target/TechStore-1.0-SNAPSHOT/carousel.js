document.addEventListener("DOMContentLoaded", function () {
    const containers = document.querySelectorAll(".product-container");
    containers.forEach((container) => {
        const productGrid = container.querySelector(".product-grid");
        const leftBtn = container.querySelector(".left-btn");
        const rightBtn = container.querySelector(".right-btn");

        if (!productGrid) return; // Bỏ qua nếu không tìm thấy product-grid

        const productItems = productGrid.querySelectorAll(".product-item");
        if (productItems.length === 0) return;

        const itemsPerPage = 4; // Số sản phẩm hiển thị mỗi trang
        const totalPages = Math.ceil(productItems.length / itemsPerPage);
        let currentPage = 0;

        // Ẩn tất cả sản phẩm ban đầu, sau đó hiển thị trang đầu tiên
        productItems.forEach((item, index) => {
            item.style.display = "none";
        });
        updatePage();

        // Luôn hiển thị nút, nhưng vô hiệu hóa nếu không cần
        leftBtn.style.display = "flex";
        rightBtn.style.display = "flex";

        rightBtn.addEventListener("click", function () {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updatePage();
            }
            updateButtonState();
        });

        leftBtn.addEventListener("click", function () {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
            updateButtonState();
        });

        function updatePage() {
            productItems.forEach((item, index) => {
                const page = Math.floor(index / itemsPerPage);
                item.style.display = page === currentPage ? "block" : "none";
            });
        }

        function updateButtonState() {
            leftBtn.disabled = currentPage <= 0;
            rightBtn.disabled = currentPage >= totalPages - 1;
        }

        updateButtonState();

        // Thêm sự kiện resize để cập nhật khi kích thước cửa sổ thay đổi
        window.addEventListener("resize", () => {
            updatePage();
            updateButtonState();
        });
    });

    // Cố định sidebar
    const sidebar = document.querySelector(".category-sidebar");
    const navbarHeight = document.querySelector(".navbar").offsetHeight;
    window.addEventListener("scroll", function () {
        const scrollTop = window.scrollY;
        sidebar.style.top = Math.max(navbarHeight, 100 - scrollTop) + "px";
    });
});