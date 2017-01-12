export const COLLECTION_ADD_TRACK = "COLLECTION_ADD_TRACK";

export function addTracks(tracks) {
    return {
        type: COLLECTION_ADD_TRACK,
        tracks
    };
}