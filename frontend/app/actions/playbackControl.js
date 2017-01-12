export const PLAYBACK_PAUSE_REQUESTED = "PLAYBACK_PAUSE_REQUESTED";
export const PLAYBACK_PLAY_REQUESTED = "PLAYBACK_PLAY_REQUESTED";
export const PLAYLIST_PLAY_NEXT = "PLAYLIST_PLAY_NEXT";
export const PLAYLIST_PLAY_PREV = "PLAYLIST_PLAY_PREV";

export function requestPause(){
    return {
        type: PLAYBACK_PAUSE_REQUESTED
    };
}

export function requestPlay() {
    return {
        type: PLAYBACK_PLAY_REQUESTED
    };
}

export function playNext() {
    return {
        type: PLAYLIST_PLAY_NEXT
    }
}

export function playPrev() {
    return {
        type: PLAYLIST_PLAY_PREV
    }
}