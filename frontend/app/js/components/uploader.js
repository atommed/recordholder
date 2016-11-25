import Uploader from './presentational/uploader'
import {connect} from 'react-redux'
import {addTrackToPlaylist, addTrackToDB, trackUploadStart, trackUploadSuccess, trackUploadFailure} from '../actions'

function mapDispatchToProps(dispatch, ownProps){
    return {
        onUploadSuccess: (resp) => {
            dispatch(trackUploadSuccess(resp));
            dispatch(addTrackToDB(resp));
            dispatch(addTrackToPlaylist(resp.id, 0));
        },
        onUploadFailure: (code, resp)=> {
            dispatch(trackUploadFailure(code, resp))
        },
        onUploadStarted: ()=>{
            dispatch(trackUploadStart())
        }
    }
}

export default connect(null,mapDispatchToProps)(Uploader);