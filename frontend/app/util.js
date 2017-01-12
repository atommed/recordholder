import * as Cookies from 'js-cookie'

function ensuringCsrf(action) {
    const token = Cookies.get("Csrf-Token");
    if(token !== undefined) action();
    else {
        $.ajax({
            url: "/api/csrfToken"
        }).done(()=>{
            action();
        })
    }
}

export {
    ensuringCsrf
}