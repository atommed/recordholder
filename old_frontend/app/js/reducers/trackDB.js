import {DB_TRACK_ADD} from '../actions'

export default function trackDB(state={}, action) {
    switch(action.type){
        case DB_TRACK_ADD:
            //TODO: What if track is already in DB?
            const newState = {...state};
            newState[action.track.id] = action.track;
            return newState;
            break;
        default: return state;break;
    }
}