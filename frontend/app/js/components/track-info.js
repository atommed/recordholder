import TrackInfo from './presentational/track-info'
import {connect} from 'react-redux'

function mapStateToProps(state){
    const pl = state.playlist;
    const db = state.trackDB;

    const tid = pl.trackIDs[pl.currentPos];
    const track = db[tid];
    const tags = track !== undefined ? track.tags : null

    return {
        tags
    }
}

export default connect(mapStateToProps)(TrackInfo)