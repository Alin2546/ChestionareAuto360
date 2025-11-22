document.addEventListener("DOMContentLoaded", function () {
    const questions = document.querySelectorAll('.faq-question');
    questions.forEach(q => {
        q.addEventListener('click', () => {
            const answer = q.nextElementSibling;
            answer.style.display = answer.style.display === 'block' ? 'none' : 'block';
        });
    });

    const sidebar = document.getElementById("sidebar");
    const contentWrapper = document.getElementById("content-wrapper");
    const menuToggle = document.getElementById("menu-toggle");
    const closeBtn = document.getElementById("close-btn");

    menuToggle.addEventListener("click", () => {
        sidebar.classList.add("show");
        contentWrapper.classList.add("shifted");
    });

    closeBtn.addEventListener("click", () => {
        sidebar.classList.remove("show");
        contentWrapper.classList.remove("shifted");
    });
});