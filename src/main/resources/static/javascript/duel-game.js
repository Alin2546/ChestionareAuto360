document.addEventListener('DOMContentLoaded', () => {
    let currentQuestionIndex = 0;
    let correctAnswers = 0;
    let wrongAnswers = 0;
    let waitingForNext = false;
    let answers = {};
    let timeLeft = 10;
    let timer;

    const questionTextEl = document.querySelector(".question-text");
    const optionsEl = document.querySelector(".options");
    const playerScoreEl = document.getElementById('player-score');
    const opponentScoreEl = document.getElementById('opponent-score');
    const timeRemainingEl = document.getElementById("timeRemaining");
    const submitBtn = document.getElementById('submitBtn');
    const modifyBtn = document.getElementById('modifyBtn');
    const homeBtn = document.getElementById("homeBtn");

    let duelState = {
        player: { score: 0 },
        opponent: { score: 0 },
        questions: questions
    };

    function disableButtons() {
        submitBtn.disabled = true;
        submitBtn.classList.add("inactive");
        modifyBtn.disabled = true;
        modifyBtn.classList.add("inactive");
    }

    function enableButtons() {
        submitBtn.disabled = false;
        submitBtn.classList.remove("inactive");
        modifyBtn.disabled = false;
        modifyBtn.classList.remove("inactive");
    }

    function startTimer() {
        clearInterval(timer);
        timeLeft = 10;
        timeRemainingEl.textContent = timeLeft;
        timer = setInterval(() => {
            timeLeft--;
            timeRemainingEl.textContent = timeLeft;
            if(timeLeft <= 0){
                clearInterval(timer);
                submitAnswer([], true);
                simulateOpponentAnswer();
            }
        }, 1000);
    }

    optionsEl.addEventListener('click', (e) => {
        const target = e.target;
        if(target.tagName === 'INPUT' && target.type === 'checkbox') {
            if(target.dataset.checkedOnce === 'true') {
                target.checked = true;
            } else {
                target.dataset.checkedOnce = 'true';
            }
        }
    });

    function loadQuestion(index) {
        const q = duelState.questions[index];
        questionTextEl.textContent = q.text;
        const img = document.getElementById("questionImage");
        if(q.imageUrl && q.imageUrl.trim() !== ""){
            img.src = q.imageUrl;
            img.style.display = "block";
        } else {
            img.style.display = "none";
        }

        optionsEl.innerHTML = `
            <label>
                <input type="checkbox" name="${q.id}" value="A" />
                <span class="option-letter">A</span>
                <span>${q.optionA}</span>
            </label>
            <label>
                <input type="checkbox" name="${q.id}" value="B" />
                <span class="option-letter">B</span>
                <span>${q.optionB}</span>
            </label>
            <label>
                <input type="checkbox" name="${q.id}" value="C" />
                <span class="option-letter">C</span>
                <span>${q.optionC}</span>
            </label>
        `;

        disableButtons();
        startTimer();

        const checkboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(cb => cb.addEventListener('change', enableButtons));
    }

    function submitAnswer(selectedValues, autoNext = false) {
        clearInterval(timer);
        const q = duelState.questions[currentQuestionIndex];
        const selected = selectedValues || [];
        answers[q.id] = selected.join(",");

        const correctValues = q.correctOption.split(",");
        const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        allCheckboxes.forEach(cb => cb.disabled = true);

        allCheckboxes.forEach(cb => {
            const label = cb.parentElement;
            const letter = label.querySelector('.option-letter');
            if(correctValues.includes(cb.value)){
                label.style.background = "#c8e6c9";
                letter.style.background = "#388e3c";
                letter.style.color = "#fff";
            } else {
                label.style.background = "#ffcdd2";
                letter.style.background = "#d32f2f";
                letter.style.color = "#fff";
            }
        });

        if(selected.sort().join(",") === correctValues.sort().join(",")) {
            correctAnswers++;
            duelState.player.score = correctAnswers;
        } else {
            wrongAnswers++;
        }

        playerScoreEl.textContent = correctAnswers;

        submitBtn.innerHTML = `<i class="fa fa-check"></i> Continuă`;
        disableButtons();
        waitingForNext = true;

        if(autoNext) {
            simulateOpponentAnswer();
            setTimeout(nextQuestion, 1000);
        }
    }

    function nextQuestion() {
        currentQuestionIndex++;
        if(currentQuestionIndex < duelState.questions.length){
            loadQuestion(currentQuestionIndex);
            submitBtn.innerHTML = `<i class="fa fa-check"></i> Trimite răspunsul`;
            waitingForNext = false;
        } else {
            finalizeDuel();
        }
    }

    function simulateOpponentAnswer() {
        const chance = 0.6;
        if(Math.random() < chance){
            duelState.opponent.score++;
        }
        opponentScoreEl.textContent = duelState.opponent.score;
    }

    submitBtn.addEventListener('click', () => {
        if(!waitingForNext){
            const selectedEls = optionsEl.querySelectorAll('input[type="checkbox"]:checked');
            const selectedValues = Array.from(selectedEls).map(el => el.value);
            submitAnswer(selectedValues, true);
        } else {
            nextQuestion();
        }
    });

    modifyBtn.addEventListener('click', () => {
        const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        allCheckboxes.forEach(cb => {
            cb.checked = false;
            cb.dataset.checkedOnce = '';
            cb.disabled = false;
        });
        enableButtons();
    });

    homeBtn.addEventListener('click', e => {
        e.preventDefault();
        if(confirm("Ești sigur că vrei să închizi duelul? Progresul nu va fi salvat.")){
            window.location.href = "/";
        }
    });

    function finalizeDuel() {
        const playerScore = duelState.player.score;
        const opponentScore = duelState.opponent.score;

        let resultText;
        if(playerScore > opponentScore){
            resultText = `Ai câștigat duelul! ${playerScore} - ${opponentScore}`;
        } else if(playerScore < opponentScore){
            resultText = `Ai pierdut duelul! ${playerScore} - ${opponentScore}`;
        } else {
            resultText = `Egalitate! ${playerScore} - ${opponentScore}`;
        }

        alert(resultText);
        window.location.href = "/";
    }

    loadQuestion(currentQuestionIndex);
});
