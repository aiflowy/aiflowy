import React, {Fragment, useState} from 'react';
import './confirmItem.css';
import {Typography} from 'antd';
import {DownloadOutlined} from "@ant-design/icons";
import confirmOther from '../../../../assets/confirm-other.png';
import confirmFile from '../../../../assets/confirm-file.png';

export type ConfirmItemMultiProps = {
    ref?: any,
    selectionDataType?: string, // text, image, video, audio, other, file
    selectionData?: any[],
    label?: string,
    value?: any,
    onChange?: (value: any[]) => void
}

export const ConfirmItemMulti: React.FC<ConfirmItemMultiProps> = (options) => {

    const {selectionDataType} = options

    const [selectedValue, setSelectedValue] = useState<any[]>([]);

    const changeValue = (value: any) => {

        const isSelected = selectedValue.includes(value);

        const newSelectedValue = isSelected
            ? selectedValue.filter(item => item !== value)
            : [...selectedValue, value];

        setSelectedValue(newSelectedValue);

        if (options.onChange) {
            options.onChange(newSelectedValue);
        }
    }

    const getIcon = (type: string) => {
        return type === 'other'? confirmOther : confirmFile
    }

    const renderSelectionComponent = () => {
        return (
            <div className="custom-radio-group">
                {options.selectionData?.map((item, i) => {
                    return (
                        <Fragment key={item + i}>
                            {selectionDataType === 'text' && <div
                                key={item + i}
                                className={`custom-radio-option ${
                                    selectedValue.includes(item) ? 'selected' : ''
                                }`}
                                style={{width: '100%', flexShrink: 0}}
                                onClick={() => changeValue(item)}
                            >
                                {item}
                            </div>}

                            {selectionDataType === 'image' && <div
                                key={item + i}
                                className={`custom-radio-option ${
                                    selectedValue.includes(item) ? 'selected' : ''
                                }`}
                                style={{padding: '0'}}
                                onClick={() => changeValue(item)}
                            >
                                <img alt={""} src={item} style={{
                                    width: '80px', height: '80px',
                                    borderRadius: '8px'
                                }}/>
                            </div>}

                            {selectionDataType === 'video' && <div
                                key={item + i}
                                className={`custom-radio-option ${
                                    selectedValue.includes(item) ? 'selected' : ''
                                }`}
                                onClick={() => changeValue(item)}
                            >
                                <video controls src={item} style={{
                                    width: '162px', height: '141px'
                                }}/>
                            </div>}

                            {selectionDataType === 'audio' && <div
                                key={item + i}
                                className={`custom-radio-option ${
                                    selectedValue.includes(item) ? 'selected' : ''
                                }`}
                                style={{width: '100%', flexShrink: 0}}
                                onClick={() => changeValue(item)}
                            >
                                <audio controls src={item} style={{
                                    width: '100%', height: '44px', marginTop: '8px'
                                }}/>
                            </div>}

                            {(selectionDataType === 'other' || selectionDataType === 'file') &&
                                <>
                                    <div
                                        key={item + i}
                                        className={`custom-radio-option ${
                                            selectedValue.includes(item) ? 'selected' : ''
                                        }`}
                                        style={{width: '100%', flexShrink: 0}}
                                        onClick={() => changeValue(item)}
                                    >
                                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                                            <div style={{width: '92%',display: 'flex', alignItems: 'center'}}>
                                                <img style={{width: '20px', height: '20px', marginRight: '8px'}} alt={""}
                                                     src={getIcon(selectionDataType)}/>
                                                <Typography.Text
                                                    ellipsis={{ tooltip: item }}
                                                >
                                                    {item}
                                                </Typography.Text>
                                            </div>
                                            <div className={'download-icon-btn'} onClick={() => {
                                                window.open(item, '_blank')
                                            }}>
                                                <DownloadOutlined />
                                            </div>
                                        </div>
                                    </div>
                                </>
                            }
                        </Fragment>
                    )
                })}
            </div>
        )
    }

    return (
        <>
            {renderSelectionComponent()}
        </>
    )
}