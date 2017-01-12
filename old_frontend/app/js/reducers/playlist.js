import {PLAYLIST_TRACK_ADD, PLAYLIST_TRACK_NEXT, PLAYLIST_TRACK_PREV} from '../actions'

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
        case PLAYLIST_TRACK_NEXT:
            if(state.trackIDs[state.currentPos + 1] !== undefined) return {
                ...state,
                currentPos: state.currentPos + 1
            }; else return state;
            break;
        case PLAYLIST_TRACK_PREV:
            if(state.trackIDs[state.currentPos - 1] !== undefined) return {
                ...state,
                currentPos: state.currentPos - 1
            }; else return state;
            break;
        default: return state;break;
    }
}