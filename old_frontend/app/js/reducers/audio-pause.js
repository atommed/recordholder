import {AUDIO_PAUSE_TOGGLED} from '../actions'

function audioPaused(state=true, action){
    switch (action.type){
        case AUDIO_PAUSE_TOGGLED: return !state; break;
        default: return state; break;
    }
}

export default audioPaused;
