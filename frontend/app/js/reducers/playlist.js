import {PLAYLIST_TRACK_ADD} from '../actions'

const initialState = {
    trackIDs: [],
    currentPos: 0
};

export default function playlist(state = initialState, action){
    switch(action.type){
        case PLAYLIST_TRACK_ADD:
            const pos = action._offset >= 0 ? action.offset : (state.trackIDs.length + action.offset);
            const newState = {...state};
            newState.trackIDs = [...state.trackIDs];
            newState.trackIDs.splice(pos,0, action.trackID);
            return newState;
        break;
        default: return state;break;
    }
}