import * as A from "../actions"

const initialState = {
    user: undefined,
    message: ""
};

export function auth(state=initialState, action){
    switch(action.type){
        case A.SIGN_IN_REQUESTED:
            return {
                message: "Trying to sign in..."
            };
            break;
        case A.SIGN_UP_REQUESTED:
            return {
                message: "Trying to sign up..."
            };
            break;

        case A.SIGN_IN_FAIL:
        case A.SIGN_UP_FAIL:
            return {
                message: action.message
            };
            break;

        case A.SIGN_IN_SUCCESS:
        case A.SIGN_UP_SUCCESS:
            return {
                message: `You are signed in as ${action.user.name}`,
                user: action.user
            };
            break;

        default:
            return state
    }
}