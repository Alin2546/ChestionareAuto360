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


    const progressFill = document.getElementById("progress-bar-fill");
    if (progressFill) {
        const progress = Number(progressFill.textContent.replace('%',''));
        progressFill.style.width = progress + "%";

        if(progress <= 49){
            progressFill.style.background = "#dc2626";
        } else if(progress <= 80){
            progressFill.style.background = "#facc15";
            progressFill.style.color = "#374151";
        } else {
            progressFill.style.background = "#16a34a";
        }
    }

    const lockedCards = document.querySelectorAll('.stats-card.locked');
    lockedCards.forEach(card => {
        card.addEventListener('click', () => {
            card.classList.remove('shake');
            void card.offsetWidth;
            card.classList.add('shake');
        });
    });

  const profileInput = document.getElementById('profileImageInput');
const profilePreview = document.getElementById('profilePreview');
const profileUrlInput = document.getElementById('profileUrlInput');
const profileUrlBtn = document.getElementById('profileUrlBtn');


profileInput.addEventListener('change', () => {
    const file = profileInput.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => {
        profilePreview.src = e.target.result;
    };
    reader.readAsDataURL(file);
});

profileUrlBtn.addEventListener('click', async () => {
    const url = profileUrlInput.value.trim();
    if (!url) return alert("Introdu un URL valid!");

    profilePreview.src = url;

    const formData = new URLSearchParams();
    formData.append("imageUrl", url);

    const response = await fetch("/update-profile-image", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData
    });

    const text = await response.text();
    alert(text);
});