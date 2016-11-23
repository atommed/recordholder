import {TRACK_UPLOAD_STARTED,TRACK_UPLOAD_SUCCEEDED, TRACK_UPLOAD_FAILED} from '../actions'

function trackUploadResult(state="", action){
    switch (action.type){
        case TRACK_UPLOAD_SUCCEEDED: return action.responseText;
        case TRACK_UPLOAD_FAILED: return action.responseText;
        case TRACK_UPLOAD_STARTED: return "Sending track to server...";
        default: return state;
    }
}

export default trackUploadResult;