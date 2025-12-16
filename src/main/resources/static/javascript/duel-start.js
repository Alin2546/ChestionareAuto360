document.addEventListener('DOMContentLoaded', () => {
    const findBtn = document.getElementById('find-opponent');
    const friendBtn = document.getElementById('coop-friend');
    const status = document.getElementById('status');

    let playerId = 1;

    findBtn.addEventListener('click', () => {
        status.textContent = "Se caută adversar...";
        fetch('/duel/find', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `userId=${playerId}`
        })
        .then(res => res.json())
        .then(duel => {
            status.textContent = "Adversar găsit! Începem duelul...";
            setTimeout(() => {
                window.location.href = `/duel/game?duelId=${duel.id}`;
            }, 1000);
        })
        .catch(err => {
            status.textContent = "Eroare la găsirea adversarului";
            console.error(err);
        });
    });

    friendBtn.addEventListener('click', () => {
        const friendId = prompt("Introdu ID-ul prietenului tău:");
        if (!friendId) return;

        status.textContent = "Se trimite invitația...";
        fetch('/duel/invite', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `userId=${playerId}&friendId=${friendId}`
        })
        .then(res => res.json())
        .then(response => {
            if(response.success){
                status.textContent = "Invitația a fost trimisă! Așteaptă acceptul.";
            } else {
                status.textContent = "Nu s-a putut trimite invitația.";
            }
        })
        .catch(err => {
            status.textContent = "Eroare la trimiterea invitației";
            console.error(err);
        });
    });
});
