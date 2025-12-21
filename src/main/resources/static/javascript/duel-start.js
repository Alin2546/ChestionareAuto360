document.addEventListener('DOMContentLoaded', () => {
    const findBtn = document.getElementById('find-opponent');
    const friendBtn = document.getElementById('coop-friend');
    const computerPlayBtn = document.getElementById('computer-play');
    const status = document.getElementById('status');

    const modal = document.getElementById('categoryModal');
    const closeBtn = document.getElementById('closeModal');

    const friendModal = document.getElementById('friendDuelContainer');
    const createBtn = document.getElementById('createDuelBtn');
    const joinBtn = document.getElementById('joinDuelBtn');
    const duelCodeSection = document.getElementById('duelCodeSection');
    const duelCodeText = document.getElementById('duelCodeText');
    const joinDuelInput = document.getElementById('joinDuelInput');
    const joinDuelConfirmBtn = document.getElementById('joinDuelConfirmBtn');
    const friendCategorySection = document.getElementById('friendCategorySection');
    const closeFriendModal = document.getElementById('closeFriendModal');
    const friendStatus = document.getElementById('friendStatus');

    let playerId = 1;
    let selectedCategory = null;

    findBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });

    computerPlayBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });

    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedCategory = btn.dataset.category;
            modal.style.display = 'none';
            startMatchmaking();
        });
    });

    function startMatchmaking() {
        status.textContent = `Se caută adversar pentru categoria ${selectedCategory}...`;
        fetch('/duel/find', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `userId=${playerId}&category=${selectedCategory}`
        })
        .then(res => res.json())
        .then(duel => {
            status.textContent = "Adversar găsit! Începem duelul...";
            setTimeout(() => {
                window.location.href = `/duel/game?category=${selectedCategory}&duelId=${duel.id}`;
            }, 1000);
        })
        .catch(err => {
            status.textContent = "Eroare la găsirea adversarului";
            console.error(err);
        });
    }

    friendBtn.addEventListener('click', () => {
        friendModal.style.display = 'flex';
        duelCodeSection.style.display = 'none';
        friendCategorySection.style.display = 'none';
        friendStatus.textContent = '';
        duelCodeText.style.display = 'block';
        joinDuelInput.style.display = 'none';
        joinDuelConfirmBtn.style.display = 'none';
        document.getElementById('friendOptions').style.display = 'block';
    });

    closeFriendModal.addEventListener('click', () => {
        friendModal.style.display = 'none';
    });

    createBtn.addEventListener('click', () => {
        friendCategorySection.style.display = 'block';
        duelCodeSection.style.display = 'none';
        friendStatus.textContent = '';
        document.getElementById('friendOptions').style.display = 'none';
    });

    document.querySelectorAll('.friend-category-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            selectedCategory = btn.dataset.category;
            const response = await fetch('/duel/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `userId=${playerId}&category=${selectedCategory}`
            });
            const data = await response.json();
            duelCodeText.innerHTML = `
                <span>Codul duelului: ${data.code}</span>
                <div class="spinner"></div>
            `;
            duelCodeSection.style.display = 'block';
            friendCategorySection.style.display = 'none';
            friendStatus.textContent = 'Așteaptă ca prietenul să se alăture...';
        });
    });

    joinBtn.addEventListener('click', () => {
        duelCodeSection.style.display = 'block';
        duelCodeText.style.display = 'none';
        joinDuelInput.style.display = 'inline-block';
        joinDuelConfirmBtn.style.display = 'inline-block';
        friendCategorySection.style.display = 'none';
        friendStatus.textContent = '';
        document.getElementById('friendOptions').style.display = 'none';
    });

    joinDuelConfirmBtn.addEventListener('click', async () => {
        const code = joinDuelInput.value.trim();
        if (!code) return friendStatus.textContent = 'Trebuie să introduci un cod!';
        const response = await fetch(`/duel/join?code=${code}`, { method: 'POST' });
        const data = await response.json();
        if (data.success) {
            friendStatus.textContent = 'Te-ai alăturat duelului!';
            window.location.href = `/duel/game?duelId=${data.duelId}`;
        } else {
            friendStatus.textContent = 'Cod invalid sau duelul nu există.';
        }
    });
});
