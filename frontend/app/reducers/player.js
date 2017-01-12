import * as A from "../actions"

const initialState = {
    playlist:{
        tracks: [2,35],
        currentPos: 0
    },
    db: {
        tracks: {
            2:{
                id: 2,
                extension: "ogg",
                title: "Test title",
                artistId: 45,
                albumId: 22
            },
            35:{
                id: 35,
                title: "sf by som",
                extension: "mp3"
            }
        },
        artists: {
            45: {
                name: "Test artist name"
            }
        },
        albums: {
            22: {
                name: "Test album name"
            }
        }
    },
    isPlaying: false
};

function pauseRequested(state){
    return {
        ...state,
        isPlaying: false
    };
}

function playRequested(state) {
    const playlist = state.playlist;
    let newPos = playlist.currentPos;
    if(playlist.currentPos == undefined && playlist.tracks.length > 0) newPos = 0;

    return {
        ...state,
        isPlaying: playlist.tracks[newPos] !== undefined,
        playlist: {
            ...playlist,
            currentPos: newPos
        }
    };
}

function playNext(state) {
    const playlist = {...state.playlist};
    playlist.currentPos += 1;
    return {
        ...state,
        playlist
    }
}

function playPrev(state) {
    const playlist = {...state.playlist};
    playlist.currentPos -= 1;
    return {
        ...state,
        playlist
    }
}

function addTracksToCollection(state, newTracks){
    const tracks = {...state.db.tracks};
    newTracks.forEach(track=>tracks[track.id] = track);
    return {
        ...state,
        db: {
            ...state.db,
            tracks
        }
    }
}

export function player(state = initialState, action) {
    switch(action.type){
        case A.PLAYBACK_PAUSE_REQUESTED: return pauseRequested(state); break;
        case A.PLAYBACK_PLAY_REQUESTED: return playRequested(state);break;
        case A.PLAYLIST_PLAY_NEXT: return playNext(state); break;
        case A.PLAYLIST_PLAY_PREV: return playPrev(state); break;

        case A.COLLECTION_ADD_TRACK: return addTracksToCollection(state, action.tracks); break;
        default:
            return state;
            break;
    }
}