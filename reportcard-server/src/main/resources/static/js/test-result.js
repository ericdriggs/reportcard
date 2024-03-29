function toggleSiblings(source) {
    var parent = source.parentElement
    for (const child of parent.children) {
        toggle(child);
    }
}

function showChildrenById(id) {

    var parent = document.getElementById(id);
    for (const child of parent.children) {
        show(child);
    }
}


function toggleChildren(parent) {
    for (const child of parent.children) {
        toggle(child);
    }
}

function toggle(obj) {
    if (obj.classList.contains("show")) {
        obj.classList.remove("show");
        obj.classList.add("hide");
    } else if (obj.classList.contains("hide")) {
        obj.classList.remove("hide");
        obj.classList.add("show");
    }
}

function show(obj) {
    if (obj.classList.contains("hide")) {
        obj.classList.remove("hide");
        obj.classList.add("show");
    }
}