export const TRACK_UPLOAD_SUCCEEDED = "TRACK_UPLOAD_SUCCEEDED";
export const TRACK_UPLOAD_FAILED = "TRACK_UPLOAD_FAILED";
export const TRACK_UPLOAD_STARTED = "TRACK_UPLOAD_STARTED";
export const AUDIO_PAUSE_TOGGLED = "AUDIO_PAUSE_TOGGLED";

export function trackUploadSuccess(responseText){
    return {
        type: TRACK_UPLOAD_SUCCEEDED,
        responseText
    }
}

export function trackUploadFailure(code, responseText) {
    return {
        type: TRACK_UPLOAD_FAILED,
        code,
        responseText
    }
}

export function trackUploadStart(){
    return {
        type: TRACK_UPLOAD_STARTED
    };
}

export function toggleAudioPause() {
    return {
        type: AUDIO_PAUSE_TOGGLED
    }
}