import {AUTHENTICATED} from '../actions'

function authentication(state = {}, action){
    switch(action.type){
        case AUTHENTICATED:
            return {
                id: action.id,
                token: action.token
            };
            break;
        default: return state; break;
    }
}

export default authentication;