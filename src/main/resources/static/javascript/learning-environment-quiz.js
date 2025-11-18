let currentQuestionIndex = 0;
let waitingForNext = false;

const questionTextEl = document.querySelector(".question-text");
const optionsEl = document.querySelector(".options");
const questionsLeftEl = document.getElementById("questionsLeft");
const totalQuestionsEl = document.getElementById("totalQuestions");
const wrongMessageEl = document.getElementById("wrongMessage");

const submitBtn = document.getElementById("submitBtn");
const modifyBtn = document.getElementById("modifyBtn");
const showAnswerBtn = document.getElementById("laterBtn");
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

optionsEl.addEventListener('click', (e) => {
    const target = e.target;
    if (target.tagName === 'INPUT' && target.type === 'checkbox') {
        if (target.dataset.checkedOnce === 'true') {
            target.checked = true;
        } else {
            target.dataset.checkedOnce = 'true';
        }
    }
});

homeBtn.addEventListener("click", e => {
    e.preventDefault();
    if (confirm("Sigur că vrei să închizi mediul de învățare? Progresul tău va fi salvat și vei putea continua mai târziu de unde ai rămas.")) {
        window.location.href = "/";
    }
});


function saveProgress() {
    fetch(`/save-progress?currentQuestionIndex=${currentQuestionIndex}&category=${selectedCategory}`, {
        method: 'POST',
        credentials: 'same-origin'
    });
}

modifyBtn.addEventListener('click', () => {
    const allCheckboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
    allCheckboxes.forEach(cb => {
        cb.checked = false;
        cb.dataset.checkedOnce = '';
    });
    disableButtons();
});

function loadQuestion(index) {
    if (!questions || questions.length === 0) {
        console.error("Nu există întrebări încărcate pentru categoria:", selectedCategory);
        return;
    }


    const q = questions[index];


    if (!q) {
        console.error("Question not found for index:", index);
        return;
    }

    questionTextEl.textContent = q.text;

    const img = document.getElementById("questionImage");

    if (q.imageUrl && q.imageUrl.trim() !== "") { img.src = q.imageUrl; img.style.display = "block"; } else { img.style.display = "none"; }

    optionsEl.innerHTML = `
            <label>
                <input type="checkbox" name="answer" value="A" />
                <span class="option-letter">A</span>
                <span>${q.optionA}</span>
            </label>
            <label>
                <input type="checkbox" name="answer" value="B" />
                <span class="option-letter">B</span>
                <span>${q.optionB}</span>
            </label>
            <label>
                <input type="checkbox" name="answer" value="C" />
                <span class="option-letter">C</span>
                <span>${q.optionC}</span>
            </label>
        `;
    totalQuestionsEl.textContent = questions.length;
    questionsLeftEl.textContent = questions.length - currentQuestionIndex;
    disableButtons();

    const checkboxes = optionsEl.querySelectorAll('input[type="checkbox"]');
    checkboxes.forEach(cb => cb.addEventListener('change', enableButtons));
}

function showCorrectAnswers() {
    const correctValues = questions[currentQuestionIndex].correctOption.split(",");

    optionsEl.querySelectorAll("input").forEach(cb => {
        cb.checked = false;
        cb.disabled = true;
        const label = cb.parentElement;
        const letter = label.querySelector(".option-letter");

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

    modifyBtn.disabled = true;
    modifyBtn.classList.add("inactive");
    showAnswerBtn.disabled = true;
    showAnswerBtn.classList.add("inactive");

    submitBtn.disabled = false;
    submitBtn.classList.remove("inactive");
    submitBtn.innerHTML = `<i class="fa fa-check"></i> Continuă`;

    waitingForNext = true;
}

window.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await fetch(`/get-progress?category=${selectedCategory}`, { credentials: 'same-origin' });
        const startIndex = await res.json();
        currentQuestionIndex = startIndex || 0;
    } catch (err) {
        console.warn("Progress fetch failed:", err);
        currentQuestionIndex = 0;
    }
    loadQuestion(currentQuestionIndex);
});

function nextQuestion() {
    currentQuestionIndex++;
    if (currentQuestionIndex < questions.length) {
        loadQuestion(currentQuestionIndex);

        submitBtn.innerHTML = `<i class="fa fa-check"></i> Trimite răspunsul`;
        waitingForNext = false;


        modifyBtn.disabled = true;
        modifyBtn.classList.add("inactive");


        showAnswerBtn.disabled = false;
        showAnswerBtn.classList.remove("inactive");


        optionsEl.querySelectorAll("input").forEach(cb => {
            cb.checked = false;
            cb.disabled = false;
            cb.dataset.checkedOnce = '';
        });

    } else {
        document.getElementById("finalModal").style.display = "flex";
        document.querySelector("main").style.display = "none";
        document.querySelector("footer").style.display = "none";

        fetch('/complete-learning', {
            method: 'POST',
            credentials: 'same-origin'
        });
    }
}

submitBtn.addEventListener("click", () => {
    modifyBtn.disabled = true;
    modifyBtn.classList.add("inactive");

    if (!waitingForNext) {
        const selected = Array.from(optionsEl.querySelectorAll("input:checked")).map(cb => cb.value);
        questions[currentQuestionIndex].selectedAnswers = selected;
        if (selected.length === 0) return;
        const correctValues = questions[currentQuestionIndex].correctOption.split(",");
        if (selected.sort().join(",") === correctValues.sort().join(",")) {
            wrongMessageEl.style.display = "none";


            nextQuestion();
            saveProgress();
        } else {
            wrongMessageEl.style.display = "block";
            optionsEl.querySelectorAll("input").forEach(cb => cb.checked = false);
            disableButtons();
        }
    } else {
        nextQuestion();
        saveProgress();

        waitingForNext = false;
    }
});
showAnswerBtn.addEventListener("click", () => showCorrectAnswers());
loadQuestion(currentQuestionIndex);
