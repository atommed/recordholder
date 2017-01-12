import AlbumView from './presentational/AlbumView'
import {addTrackToDB} from '../actions'
import {connect} from 'react-redux'

function mapDispatchToProps(dispatch) {
    return {
        saveTrack: function (track) {
            dispatch(addTrackToDB(track))
        }
    }
}

export default connect(null, mapDispatchToProps)(AlbumView);