import React, {PropTypes} from 'react'
import * as Cookies from 'js-cookie'

class Uploader extends React.Component{
    handleSubmit(ev){
        ev.preventDefault();
        const formData = new FormData(this.form);
        const req = new XMLHttpRequest();
        req.open("POST", "/api/tracks/upload", true);
        req.setRequestHeader('Csrf-Token', Cookies.get('Csrf-Token'));
        req.onreadystatechange=()=>{
            if(req.readyState != 4) return;
            if(req.status == 200)
                this.props.onUploadSuccess(JSON.parse(req.response));
            else
                this.props.onUploadFailure(req.status, req.response);

        };
        (f=>{if(f) f()})(this.props.onUploadStarted);
        req.send(formData);
    };

    constructor(){
        super();
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render(){
        return (
            <form ref={(form)=>{this.form = form;}}
                  onSubmit={this.handleSubmit}
                  encType="multipart/form-data"
                  method="post">
                <div className="file-field input-field">
                    <div className="colored btn waves-effect">
                        <span>Select track:</span>
                        <input type="file" name="track" required />
                    </div>
                    <div className="file-path-wrapper">
                        <input className="file-path validate" type="text" />
                    </div>
                </div>
                <button className="colored btn waves-effect" type="submit" >
                    <i className="material-icons">file_upload</i>
                    Upload!
                </button>
            </form>
        )
    }
}

Uploader.propTypes={
    onUploadSuccess: PropTypes.func.isRequired,
    onUploadFailure: PropTypes.func.isRequired,
    onUploadStarted: PropTypes.func,
};

export default Uploader;
