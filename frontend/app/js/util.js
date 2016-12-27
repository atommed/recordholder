function setContentType(req, ct){
    req.setRequestHeader("Content-Type", ct);
}

function setAJAX(req, ct) {
    setContentType(req,"application/json;charset=UTF-8")
}

export {
    setAJAX, setContentType
}