import Track from './presentational/Track'
import {addTrackToPlaylist} from '../actions'
import {connect} from 'react-redux'

function mapDispatchToProps(dispatch) {
    return {
        handlePlay: function (trackId) {
            dispatch(addTrackToPlaylist(trackId, 0));
        }
    }
}

export default connect(null, mapDispatchToProps)(Track)