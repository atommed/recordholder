import  Player from "./presentational/Player"
import {connect} from "react-redux";

export default connect(
    function mapStateToProps(state) {
        const playlist = state.player.playlist;
        const trackId = playlist.tracks[playlist.currentPos];
        const track = state.player.db.tracks[trackId];
        const src = `storage/tracks/${track.id}.${track.extension}`;
        const album = state.player.db.albums[track.albumId];
        const artist = state.player.db.artists[track.artistId];
        return {
            src,
            title: track.title,
            albumName: album ? album.name : undefined,
            artistName: artist ? artist.name : undefined,
            isPaused: !state.player.isPlaying
        }
    },
    function mapDispatchToProps(dispatch) {
        return {

        }
    }
)(Player)