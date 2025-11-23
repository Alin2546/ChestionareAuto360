
const semiCircle = document.getElementById('semiCircle');
const gaugeValue = document.getElementById('gaugeValue');
const gaugeText = document.getElementById('gaugeText');
let currentVal = 0;
const maxScore = 100;

function interpolateColor(pct) {
    const colors = [
        { pct: 0.0, color: { r: 255, g: 0, b: 0 } },
        { pct: 0.25, color: { r: 255, g: 127, b: 0 } },
        { pct: 0.5, color: { r: 255, g: 215, b: 0 } },
        { pct: 0.75, color: { r: 154, g: 205, b: 50 } },
        { pct: 1.0, color: { r: 0, g: 176, b: 80 } }
    ];
    for (let i = 1; i < colors.length; i++) {
        if (pct <= colors[i].pct) {
            const lower = colors[i - 1];
            const upper = colors[i];
            const range = upper.pct - lower.pct;
            const rangePct = (pct - lower.pct) / range;
            const r = Math.round(lower.color.r + rangePct * (upper.color.r - lower.color.r));
            const g = Math.round(lower.color.g + rangePct * (upper.color.g - lower.color.g));
            const b = Math.round(lower.color.b + rangePct * (upper.color.b - lower.color.b));
            return `rgb(${r},${g},${b})`;
        }
    }
    return 'rgb(0,176,80)';
}

function animateGauge() {
    function step() {
        if (currentVal < gaugeVal) {
            currentVal += 1;
            if (currentVal > gaugeVal) currentVal = gaugeVal;
            const angle = (currentVal / maxScore) * 180;
            const color = interpolateColor(currentVal / 100);
            semiCircle.style.background = `conic-gradient(${color} 0deg ${angle}deg, #e5e7eb ${angle}deg 180deg)`;
            gaugeValue.textContent = Math.round(currentVal) + ' %';
            if (currentVal < 30) gaugeText.textContent = "Începe să rezolvi chestionarele! 📘";
            else if (currentVal < 60) gaugeText.textContent = "Progres bun! Continuă să înveți! 💪";
            else if (currentVal < 80) gaugeText.textContent = "Ești aproape pregătit! 🌟";
            else gaugeText.textContent = "Pregătit pentru examen! 🎉";
            requestAnimationFrame(step);
        }
    }
    requestAnimationFrame(step);
}

window.addEventListener('DOMContentLoaded', () => {
    animateGauge();

    const leaderboardList = document.getElementById('leaderboardList');
    if (leaderboardList) {
        const currentUserEntry = leaderboardList.querySelector('.current-user');
        if (currentUserEntry) {
            const entryHeight = currentUserEntry.offsetHeight;
            const offset = entryHeight * 8;
            const scrollPos = currentUserEntry.offsetTop - offset;
            leaderboardList.scrollTop = Math.max(0, scrollPos);
        }
    }

    const modal = document.getElementById('userModal');
    const modalUser = document.getElementById('modalUser');
    const modalTotal = document.getElementById('modalTotal');
    const modalPassed = document.getElementById('modalPassed');
    const modalFailed = document.getElementById('modalFailed');
    const closeModal = document.getElementById('closeModal');

    document.querySelectorAll('.leaderboard-entry').forEach(entry => {
        entry.addEventListener('click', () => {
            const phone = entry.getAttribute('data-user');
            fetch(`/statistics/user/${phone}`)
                .then(res => res.json())
                .then(data => {
                    modalUser.textContent = `Utilizator: ${phone}`;
                    modalTotal.textContent = data.total;
                    modalPassed.textContent = data.passed;
                    modalFailed.textContent = data.failed;
                    modal.style.display = 'flex';
                });
        });
    });

    if (closeModal) {
        closeModal.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    const showBtn = document.getElementById('showLeaderboardBtn');
    const leaderboard = document.querySelector('.leaderboard-container');

    if (showBtn && leaderboard) {
        if (window.innerWidth <= 575.98) {
            leaderboard.style.display = 'none';
        }

        showBtn.addEventListener('click', () => {
            if (leaderboard.style.display === 'none') {
                leaderboard.style.display = 'block';
                showBtn.textContent = 'Ascunde Clasamentul';
            } else {
                leaderboard.style.display = 'none';
                showBtn.textContent = 'Vezi Clasamentul';
            }
        });
    }
});
