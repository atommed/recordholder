import {connect} from "react-redux"

import {addTracks} from "../actions"
import Uploader from "./presentational/Uploader"

export default connect(
    null,
    function mapDispatchToProps(dispatch) {
        return {
            onUploadSuccess: function (resp) {
                console.log(resp);
                dispatch(addTracks([resp]))
            }
        }
    }
)(Uploader)