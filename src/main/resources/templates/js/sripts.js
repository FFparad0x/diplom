console.log("Не забудь назначить куд-нибудь initClik");


function Search(value) {
    var start = event.target;
    var parent = start.parentElement;
    var searchprase = parent.getElementById("selectorsearch").value;
    var elements = parent.getElementsByTagName("LABEL")

    for (var i = 0; i < elements.length; i++) {
        if (elements[i].textContent.toUpperCase().includes(searchprase.toUpperCase())) {
            var nested = elements[i].parentNode.children;
            for (var j = 0; j < nested.length; j++) {
                if (nested[j].tagName === "LABEL" || nested[j].tagName === "INPUT") {
                    nested[j].hidden = false;
                }
            }
        }
        else {
            var nested = elements[i].parentNode.children;
            for (var j = 0; j < nested.length; j++) {
                if (nested[j].tagName === "LABEL" || nested[j].tagName === "INPUT") {
                    nested[j].hidden = true;
                }
            }
        }
    }

    var mas = [];
    for (var i = 0; i < elements.length; i++) {
        elpa = elements[i].parentNode;
        while (elpa.parentNode.id != "selectorsbody") {
            var hide = true;
            for (var j = 0; j < elpa.children.length; j++) {
                if (elpa.children[j].hidden == false) {
                    hide = false;
                    if (elpa.id.includes("GRP")) {
                        mas.push(elpa);
                    }
                }
            }
            if (hide) {
                elpa.hidden = true;
            }
            else {
                elpa.hidden = false;
            }
            elpa = elpa.parentNode;
        }
    }
    mas.forEach(element1 => {
        var hide = true;
        Array.from(element1.children).forEach(element2 => {
            if (element2.hidden == false) {
                hide = false;
            }
        });
        if (hide === false) {
            element1.querySelector("label").hidden = false;
        }
    });
}

function InitClick() {
    document.getElementById("selectorsbody").querySelectorAll("input[type=checkbox]").forEach(element => {
        element.addEventListener("change", Click, false);
    });
}

function Seter(value){
    var fieldset = document.getElementById("value");
    var switcher = document.getElementById("rbody1");
    if(switcher.checked == false){
        fieldset.disabled = true;
    }
    else{
        fieldset.disabled = false;
    }
}

function Click() {
    var start = event.target;
    console.log(start.checked);
    if (start.checked) {  // Пометели все подряд как неполноценные эл-ты 
        start.indeterminate = false;
        start.parentElement.querySelectorAll("input[type=checkbox]").
            forEach(input => {
                input.checked = true;
                input.indeterminate = false;
            });
        start.parentElement.querySelectorAll(".container").forEach(container =>{
            container.classList.add("border-primary");
            container.classList.remove("border-grey");
        })
        let parent = start;
        do {
            let indeterminate = false;
            parent = parent.parentElement;
            if (parent.id.includes("GRP")) {
                parent.getElementsByTagName("INPUT")[0].checked = true;
                parent.querySelectorAll("input[type=checkbox]").forEach(element => {
                    if (element.checked === false)
                        indeterminate = true;
                });
                if (indeterminate)
                    parent.getElementsByTagName("INPUT")[0].indeterminate = true;
                else
                    parent.getElementsByTagName("INPUT")[0].indeterminate = false;
                parent.querySelector("div").classList.add("border-primary");
                parent.querySelector("div").classList.remove("border-grey");
            }
        }
        while (parent.id != "GRPall");


    }

    if (start.checked == false) {  // Пометели все подряд как неполноценные эл-ты 
        let parent = start;

        
        start.indeterminate = false;
        start.parentElement.querySelectorAll("input[type=checkbox]").
            forEach(input => {
                input.checked = false;
                input.indeterminate = false;
            });

        start.parentElement.querySelectorAll(".container").forEach(container =>{
            container.classList.remove("border-primary");
            container.classList.add("border-grey");
        })

        do {
            let checked = false;
            parent = parent.parentElement;
            if (parent.id.includes("GRP")) {
                parent.getElementsByTagName("INPUT")[0].indeterminate = true;
                parent.getElementsByTagName("INPUT")[0].checked = false;
                parent.querySelectorAll("input[type=checkbox]").forEach(element => {
                    if (element.checked == true) {
                        checked = true;
                    }
                });
                if (checked) {
                    parent.getElementsByTagName("INPUT")[0].checked = true;  //TODO: Поставить классы в контейнер
                    parent.getElementsByTagName("INPUT")[0].indeterminate = true;
                }
                else {
                    parent.getElementsByTagName("INPUT")[0].checked = false;
                    parent.getElementsByTagName("INPUT")[0].indeterminate = false;
                    parent.querySelector("div").classList.remove("border-primary");
                    parent.querySelector("div").classList.add("border-grey");
                }

            }
        }
        while (parent.id != "GRPall");

    }
}

