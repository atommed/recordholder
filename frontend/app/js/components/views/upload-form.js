import React, {PropTypes} from 'react'

class UploadForm extends React.Component{
    handleSubmit(ev){
        ev.preventDefault();
        const formData = new FormData(this.form);
        const req = new XMLHttpRequest();
        req.open("POST", this.props.targetURL, true);
        req.onreadystatechange=()=>{
            if(req.readyState != 4) return;
            if(req.status == 200)
                this.props.onUploadSuccess(req.response);
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
                <input type="file" required name="track"/>
                <input type="submit" value="Upload file!"/>
            </form>
        )
    }
}

UploadForm.propTypes={
    onUploadSuccess: PropTypes.func,
    onUploadFailure: PropTypes.func,
    onUploadStarted: PropTypes.func,
    targetURL: PropTypes.string.isRequired
};

export default UploadForm;