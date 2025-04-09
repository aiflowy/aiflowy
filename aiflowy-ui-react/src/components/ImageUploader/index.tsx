import React, {useEffect, useState} from 'react';
import ImgCrop from "antd-img-crop";
import {Modal, UploadFile, UploadProps} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {Uploader} from "../Uploader";

const getBase64 = (file: File): Promise<string> =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = (error) => reject(error);
    });


interface ImageUploaderProps {
    value?: string;
    onChange?: (value: string) => void;
}

const ImageUploader: React.FC<ImageUploaderProps> = ({value, onChange}) => {

    const [previewOpen, setPreviewOpen] = useState(false);
    const [previewImage, setPreviewImage] = useState('');
    const [previewTitle, setPreviewTitle] = useState('');
    const [fileList, setFileList] = useState<UploadFile[]>([]);

    useEffect(() => {
        if (value) {
            setFileList([
                {
                    uid: value,
                    status: 'done',
                    url: value,
                } as UploadFile
            ])
        } else {
            setFileList([]);
        }
    }, [value])

    const handleCancel = () => setPreviewOpen(false);

    const handlePreview = async (file: UploadFile) => {
        if (!file.url && !file.preview) {
            file.preview = await getBase64(file.originFileObj as File);
        }

        setPreviewImage(file.url || (file.preview as string));
        setPreviewOpen(true);
        setPreviewTitle(file.name || file.url!.substring(file.url!.lastIndexOf('/') + 1));
    };

    const handleChange: UploadProps['onChange'] = ({file, fileList: newFileList}) => {
        setFileList(newFileList);
        if (onChange && file.status === "done" && file.response?.path) {
            onChange(file.response.path as string);
        } else if (onChange && file.status === "removed") {
            onChange("");
        }
    }


    const uploadButton = (
        <button style={{border: 0, background: 'none', cursor: "pointer"}} type="button">
            <PlusOutlined/>
            <div style={{marginTop: 8}}>上传</div>
        </button>
    );


    return (
        <>
            <ImgCrop rotationSlider aspectSlider showReset>
                <Uploader
                    listType="picture-circle"
                    fileList={fileList}
                    onPreview={handlePreview}
                    onChange={handleChange}
                    itemRender={(originNode) => {
                        return <div className={"form-item-image-uploader"}>{originNode}</div>
                    }}
                    maxCount={1}
                >
                    {fileList.length >= 1 ? null : uploadButton}
                </Uploader>
            </ImgCrop>
            <Modal open={previewOpen} title={previewTitle} footer={null} onCancel={handleCancel}>
                <img alt="" style={{width: '100%'}} src={previewImage}/>
            </Modal>
        </>
    );
};

export default ImageUploader;
