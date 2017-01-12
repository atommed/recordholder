export const SIGN_IN_REQUESTED = "SIGN_IN_REQUESTED";
export const SIGN_UP_REQUESTED = "SIGN_UP_REQUESTED";
export const SIGN_IN_SUCCESS = "SIGN_IN_SUCCESS";
export const SIGN_UP_SUCCESS = "SIGN_UP_SUCCESS";
export const SIGN_IN_FAIL = "SIGN_IN_FAIL";
export const SIGN_UP_FAIL = "SIGN_UP_FAIL";

function requestSignIn(){
    return {
        type: SIGN_IN_REQUESTED,
    }
}

function requestSignUp(){
    return {
        type: SIGN_UP_REQUESTED,
    }
}

function signInSuccess(user){
    return{
        type: SIGN_IN_SUCCESS,
        user
    }
}

function signUpSuccess(user){
    return{
        type: SIGN_UP_SUCCESS,
        user
    }
}

function signInFail(message) {
    return {
        type: SIGN_IN_FAIL,
        message
    }
}
function signUpFail(message) {
    return {
        type: SIGN_UP_FAIL,
        message
    }
}


export {
    requestSignIn,
    requestSignUp,
    signInSuccess,
    signUpSuccess,
    signInFail,
    signUpFail
}