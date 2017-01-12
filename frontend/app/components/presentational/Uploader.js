import React from 'react'

class Uploader extends React.Component{
    handleSubmit = (ev)=>{
        ev.preventDefault();
        const formData = new FormData(this.form);
        (f=>{if(f) f()})(this.props.onUploadStarted);
        $.ajax({
            url: "/api/tracks/upload",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            xhr:  () => {
                const xhr = new XMLHttpRequest();
                xhr.upload.addEventListener("progress", evt => {
                    if (evt.lengthComputable) {
                        const percentComplete = evt.loaded / evt.total * 100;
                        this.setState({progress: percentComplete});
                    }
                }, false);
                return xhr;
            }
        }).done(resp=>{
            this.props.onUploadSuccess(resp);
            this.setState({progress: undefined})
        }).fail(this.props.onUploadFailure);
    };

    constructor(){
        super();
        this.state = {};
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
                {this.state.progress &&
                    (<div className="progress">
                        <div className="determinate" style={{width: this.state.progress + "%"}}></div>
                    </div>)
                }
            </form>
        )
    }
}

Uploader.propTypes={
    onUploadSuccess: React.PropTypes.func.isRequired,
    onUploadFailure: React.PropTypes.func,
    onUploadStarted: React.PropTypes.func,
};

export default Uploader;