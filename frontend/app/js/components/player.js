import {connect} from 'react-redux'
import Player from './presentational/player'

function getTrackSrc(track){
    if(track === undefined) return null;
    else return `storage/tracks/${track.id}.${track.extension}`;
}

function getTrackCover(track){
    if(track === undefined) return null;
    else return `storage/covers/${track.id}.jpg`
}

function getCurrentTrack(trackDB, playlist){
    const currendID = playlist.trackIDs[playlist.currentPos];
    return trackDB[currendID];
}

function mapStateToProps(state){
    const track = getCurrentTrack(state.trackDB, state.playlist);
    return {
        pause: state.audioPaused,
        src: getTrackSrc(track),
        coverSrc: getTrackCover(track)
    }
}
export default connect(mapStateToProps)(Player);