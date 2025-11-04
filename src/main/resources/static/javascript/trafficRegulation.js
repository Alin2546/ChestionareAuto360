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