export const TRACK_UPLOAD_STARTED = "TRACK_UPLOAD_STARTED";
export const TRACK_UPLOAD_SUCCEEDED = "TRACK_UPLOAD_SUCCEEDED";
export const TRACK_UPLOAD_FAILED = "TRACK_UPLOAD_FAILED";
export const AUDIO_PAUSE_TOGGLED = "AUDIO_PAUSE_TOGGLED";

export const PLAYLIST_TRACK_ADD = "PLAYLIST_TRACK_ADD";
export const PLAYLIST_TRACK_NEXT = "PLAYLIST_TRACK_NEXT";
export const PLAYLIST_TRACK_PREV = "PLAYLIST_TRACK_PREV";

export const DB_TRACK_ADD = "DB_TRACK_ADD";

export const AUTHENTICATED = "AUTHENTICATED";

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
    };
}

export function addTrackToPlaylist(trackID, offset){
    return {
        type: PLAYLIST_TRACK_ADD,
        trackID,
        offset
    };
}

export function addTrackToDB(track) {
    return {
        type: DB_TRACK_ADD,
        track
    };
}

export function playListSkip(isForvard){
    const type = isForvard ? PLAYLIST_TRACK_NEXT : PLAYLIST_TRACK_PREV;
    return {type}
}

export function authenticate(userId, roles){
    return {
        type: AUTHENTICATED,
        userId,
        roles
    }
}