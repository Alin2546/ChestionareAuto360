document.addEventListener('DOMContentLoaded', () => {
    const computerPlayBtn = document.getElementById('computer-play');
    const friendBtn = document.getElementById('coop-friend');
    const modal = document.getElementById('categoryModal');
    const closeBtn = document.getElementById('closeModal');
    const status = document.getElementById('status');

    const friendModal = document.getElementById('friendDuelContainer');
    const createBtn = document.getElementById('createDuelBtn');
    const joinBtn = document.getElementById('joinDuelBtn');
    const duelCodeSection = document.getElementById('duelCodeSection');
    const duelCodeText = document.getElementById('duelCodeText');
    const joinDuelInput = document.getElementById('joinDuelInput');
    const joinDuelConfirmBtn = document.getElementById('joinDuelConfirmBtn');
    const friendCategorySection = document.getElementById('friendCategorySection');
    const friendStatus = document.getElementById('friendStatus');
    const closeFriendModal = document.getElementById('closeFriendModal');
    const friendOptions = document.getElementById('friendOptions');

    let playerId = USER_ID;
    let selectedCategory = null;
    let duelId = null;
    let currentFlow = null;


    computerPlayBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
        currentFlow = 'computer';
    });

    closeBtn.addEventListener('click', () => modal.style.display = 'none');

    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedCategory = btn.dataset.category;
            modal.style.display = 'none';

            if (currentFlow === 'computer') {
                startComputerMatch();
                currentFlow = null;
            }
        });
    });

    function startComputerMatch() {
        status.textContent = `Se caută adversar pentru categoria ${selectedCategory}...`;

        const bodyData = new URLSearchParams();
        if (playerId) bodyData.append('userId', playerId);
        bodyData.append('category', selectedCategory);

        fetch('/duel/computer', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: bodyData.toString()
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
        friendOptions.style.display = 'block';
        currentFlow = 'friend';
    });

    closeFriendModal.addEventListener('click', () => friendModal.style.display = 'none');

    createBtn.addEventListener('click', () => {
        friendCategorySection.style.display = 'block';
        duelCodeSection.style.display = 'none';
        friendOptions.style.display = 'none';
        friendStatus.textContent = '';
    });

    document.querySelectorAll('.friend-category-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            if (currentFlow !== 'friend') return;

            selectedCategory = btn.dataset.category;
            const body = new URLSearchParams();
            if (playerId) body.append('userId', playerId);
            body.append('category', selectedCategory);
            const response = await fetch('/duel/create-friend', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: body.toString()
            });
            const data = await response.json();

            duelId = data.duelId;
            duelCodeText.innerHTML = `<span>Codul duelului: ${data.code}</span><div class="spinner"></div>`;
            duelCodeSection.style.display = 'block';
            friendCategorySection.style.display = 'none';
            friendStatus.textContent = 'Așteaptă ca prietenul să se alăture...';


            const interval = setInterval(async () => {
                const res = await fetch(`/duel/json/${duelId}`);
                const duel = await res.json();
                if (duel.status === 'IN_PROGRESS') {
                    clearInterval(interval);

                    window.location.href = `/duel/game?duelId=${duelId}&category=${duel.category}`;
                }
            }, 2000);
        });
    });

    joinBtn.addEventListener('click', () => {
        duelCodeSection.style.display = 'block';
        duelCodeText.style.display = 'none';
        joinDuelInput.style.display = 'inline-block';
        joinDuelConfirmBtn.style.display = 'inline-block';
        friendCategorySection.style.display = 'none';
        friendStatus.textContent = '';
        friendOptions.style.display = 'none';
    });

    joinDuelConfirmBtn.addEventListener('click', async () => {
        const code = joinDuelInput.value.trim();
        if (!code) return friendStatus.textContent = 'Trebuie să introduci un cod!';

        const body = new URLSearchParams();
        if (playerId) body.append('userId', playerId);
        body.append('code', code);

        const response = await fetch('/duel/join-by-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: body.toString()
        });
        const data = await response.json();
        if (data.success) {
            friendStatus.textContent = 'Te-ai alăturat duelului!';
            window.location.href = `/duel/game?duelId=${data.duelId}&category=${data.category}`;
        } else {
            friendStatus.textContent = 'Cod invalid sau duelul nu există.';
        }
    });
});
