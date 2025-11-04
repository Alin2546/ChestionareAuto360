  /*<![CDATA[*/
    const questions = /*[[${questions}]]*/ [];
    const quizName = /*[[${quizName}]]*/ "";


    let currentQuestionIndex = 0;
    let correctAnswers = 0;
    let wrongAnswers = 0;

    const questionTextEl = document.querySelector(".question-text");
    const optionsEl = document.querySelector(".options");
    const questionsLeftEl = document.getElementById('questionsLeft');
    const correctAnswersEl = document.getElementById('correctAnswers');
    const wrongAnswersEl = document.getElementById('wrongAnswers');
    const submitBtn = document.getElementById('submitBtn');

    const totalQuestionsField = document.getElementById("totalQuestionsField");
    const correctAnswersField = document.getElementById("correctAnswersField");
    const wrongAnswersField = document.getElementById("wrongAnswersField");
    const categoryField = document.getElementById("categoryField");

    const modifyBtn = document.getElementById('modifyBtn');
    const laterBtn = document.getElementById('laterBtn');
    const homeBtn = document.getElementById("homeBtn");


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


let timeLeft = 1800;
const timeRemainingEl = document.getElementById("timeRemaining");
const timeWarningSound = new Audio("/sounds/timeleft.mp3");

function formatTime(seconds) {
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}:${sec < 10 ? "0" : ""}${sec}`;
}

let fiveMinuteWarningPlayed = false;
let countdownInterval;

       function countdown() {
    if (timeLeft > 0) {
        timeLeft--;
        timeRemainingEl.textContent = formatTime(timeLeft);

        if (timeLeft <= 300) {
            timeRemainingEl.style.color = "#d32f2f";

            if (!fiveMinuteWarningPlayed) {
                timeWarningSound.play().catch(err => console.log("Sound error:", err));
                fiveMinuteWarningPlayed = true;
            }
        } else {
            timeRemainingEl.style.color = "#388e3c";
        }
    } else {
        clearInterval(countdownInterval);
        alert('Timpul pentru chestionar a expirat!');
        finalizeQuiz(true);
    }
}

countdownInterval = setInterval(countdown, 1000);


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

    homeBtn.addEventListener("click", (e) => {
        e.preventDefault();
        if (confirm("Ești sigur că vrei să închizi acest chestionar? Progresul nu va fi salvat.")) {
            window.location.href = "/";
        }
    });

    modifyBtn.addEventListener('click', () => {
        const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        allCheckboxes.forEach(cb => {
            cb.checked = false;
            cb.dataset.checkedOnce = '';
        });
        disableButtons();
    });

     function loadQuestion(index){
        const q = questions[index];
        questionTextEl.textContent = q.text;
        const img = document.getElementById("questionImage");

        if (q.imageUrl && q.imageUrl.trim() !== "") { img.src = q.imageUrl; img.style.display = "block"; } else { img.style.display = "none"; }

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
        questionsLeftEl.textContent = questions.length - currentQuestionIndex;
        disableButtons();

        const checkboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(cb => cb.addEventListener('change', enableButtons));
    }

  function finalizeQuiz(isFailed = false) {
    totalQuestionsField.value = questions.length;
    correctAnswersField.value = correctAnswers;
    wrongAnswersField.value = wrongAnswers;
    categoryField.value = quizName;

    let message = "";
    let isWin = true;

    if (isFailed) {
        message = "Ați fost RESPINS la acest chestionar!";
        isWin = false;
    } else {
        message = `Ai fost declarat ADMIS! Scorul tău: ${correctAnswers}/${questions.length}`;
    }

     const payload = {
        category: quizName,
        answers: answers
    };

    fetch('/quiz/submit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => response.json())
    .then(data => {
        console.log("Rezultat salvat:", data);
    })
    .catch(err => console.error("Eroare la trimiterea rezultatului:", err));

    for (let el of document.body.children) {
        if(el.id !== "finalMessage") el.style.display = "none";
    }

    const finalMessageEl = document.getElementById("finalMessage");
    const finalTextEl = document.getElementById("finalText");
    finalMessageEl.style.display = "flex";
    finalTextEl.textContent = message;
    finalTextEl.style.color = isWin ? "#388e3c" : "#d32f2f";
}

    let waitingForNext = false;
    let answers = {};

    submitBtn.addEventListener('click', () => {
        if (!waitingForNext) {
            const selectedEls = optionsEl.querySelectorAll('input[type="checkbox"]:checked');
            if (selectedEls.length === 0) return;

            const selectedValues = Array.from(selectedEls).map(el => el.value);
             const questionId = questions[currentQuestionIndex].id;
           answers[questionId] = selectedValues.join(",");

            const correctValues = questions[currentQuestionIndex].correctOption.split(",");

            const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
            allCheckboxes.forEach(cb => cb.disabled = true);

            allCheckboxes.forEach(cb => {
                const label = cb.parentElement;
                const letter = label.querySelector('.option-letter');

                if (correctValues.includes(cb.value)) {
                    label.style.background = "#c8e6c9";
                    letter.style.background = "#388e3c";
                    letter.style.color = "#fff";
                } else {
                    label.style.background = "#ffcdd2";
                    letter.style.background = "#d32f2f";
                    letter.style.color = "#fff";
                }
            });

           if (selectedValues.sort().join(",") === correctValues.sort().join(",")) {
    correctAnswers++;
} else {
    wrongAnswers++;
    if (wrongAnswers >= 5) {
        finalizeQuiz(true);
        return;
    }
}
            correctAnswersEl.textContent = correctAnswers;
            wrongAnswersEl.textContent = wrongAnswers;

            submitBtn.innerHTML = `<i class="fa fa-check"></i> Continuă`;
            modifyBtn.disabled = true;
            modifyBtn.style.background = "#ccc";
            modifyBtn.style.color = "#666";

            laterBtn.disabled = true;
            laterBtn.style.background = "#ccc";
            laterBtn.style.color = "#666";

            waitingForNext = true;

        } else {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                loadQuestion(currentQuestionIndex);
                disableButtons();
                submitBtn.innerHTML = `<i class="fa fa-check"></i> Trimite răspunsul`;

                const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
                allCheckboxes.forEach(cb => {
                    cb.checked = false;
                    cb.disabled = false;
                });

                modifyBtn.disabled = false;
                modifyBtn.style.background = "transparent";
                modifyBtn.style.color = "#d32f2f";

                laterBtn.disabled = false;
                laterBtn.style.background = "transparent";
                laterBtn.style.color = "#555";

                waitingForNext = false;
            } else {
                finalizeQuiz();
            }
        }
    });

    laterBtn.addEventListener('click', () => {
        const currentQuestion = questions[currentQuestionIndex];
        questions.push(currentQuestion);
        questions.splice(currentQuestionIndex, 1);

        if (questions.length > 0) {
            if (currentQuestionIndex >= questions.length) currentQuestionIndex = 0;
            loadQuestion(currentQuestionIndex);
        }

        const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
        allCheckboxes.forEach(cb => {
            cb.checked = false;
            cb.disabled = false;
            cb.dataset.checkedOnce = '';
        });

        modifyBtn.disabled = false;
        modifyBtn.style.background = "transparent";
        modifyBtn.style.color = "#d32f2f";

        laterBtn.disabled = false;
        laterBtn.style.background = "transparent";
        laterBtn.style.color = "#555";

        submitBtn.innerHTML = `<i class="fa fa-check"></i> Trimite răspunsul`;
        waitingForNext = false;
    });

    loadQuestion(currentQuestionIndex);
    /*]]>*/