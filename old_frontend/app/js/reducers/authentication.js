import {AUTHENTICATED} from '../actions'

function authentication(state = null, action){
    switch(action.type){
        case AUTHENTICATED:
            return {
                userId: action.userId,
                name: action.name,
                roles: action.roles
            };
            break;
        default: return state; break;
    }
}

export default authentication;