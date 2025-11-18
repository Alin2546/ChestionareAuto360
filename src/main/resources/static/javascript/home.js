const sidebar = document.getElementById("sidebar");
const contentWrapper = document.getElementById("content-wrapper");
const menuToggle = document.getElementById("menu-toggle");
const closeBtn = document.getElementById("close-btn");
const rightPanel = document.querySelector(".right-panel");
const rightToggle = document.getElementById("right-panel-toggle");

if (rightToggle && rightPanel) {
  rightToggle.addEventListener("click", () => {
    rightPanel.classList.toggle("show");
  });
}

if (menuToggle && sidebar && contentWrapper) {
  menuToggle.addEventListener("click", () => {
    sidebar.classList.add("show");
    contentWrapper.classList.add("shifted");
  });
}

if (closeBtn && sidebar && contentWrapper) {
  closeBtn.addEventListener("click", () => {
    sidebar.classList.remove("show");
    contentWrapper.classList.remove("shifted");
  });
}

document.addEventListener("scroll", () => {
  const socials = document.querySelector(".side-socials");
  const footer = document.querySelector("footer");

  if (!socials || !footer) return;

  const footerRect = footer.getBoundingClientRect();
  const windowHeight = window.innerHeight;

  if (footerRect.top < windowHeight) {
    socials.classList.add("hidden");
  } else {
    socials.classList.remove("hidden");
  }
});
