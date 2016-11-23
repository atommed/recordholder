import UploadForm from './views/upload-form'
import {connect} from 'react-redux'
import {trackUploadStart, trackUploadSuccess, trackUploadFailure} from '../actions'

function mapDispatchToProps(dispatch, ownProps){
    return {
        onUploadSuccess: (resp) => {
            dispatch(trackUploadSuccess(resp))
        },
        onUploadFailure: (code, resp)=> {
            dispatch(trackUploadFailure(code, resp))
        },
        onUploadStarted: ()=>{
            dispatch(trackUploadStart())
        }
    }
}

const Uploader = connect(null,mapDispatchToProps)(UploadForm);

export default Uploader;