CREATE TABLE IF NOT EXISTS download_segment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    download_task_id INT NOT NULL,
    segment_number INT NOT NULL,
    segment_name VARCHAR(255) ,
    segment_path VARCHAR(255) NOT NULL,
    status VARCHAR(255) ,
    size  VARCHAR(25),
    save_directory VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
    is_valid VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS download_task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    download_url VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    size VARCHAR(255) ,
    save_directory VARCHAR(255) NOT NULL,
    temp_directory VARCHAR(255) NOT NULL,
    status VARCHAR(20) ,
    time_consuming  VARCHAR(20),
    suffix VARCHAR(10) ,
    progress VARCHAR(10) ,
    m3u8_key VARCHAR(255),
    key_bytes VARCHAR(255),
    download_bytes BIGINT,
    total_file_size BIGINT,
    thread_count INT,
    is_bytes BOOLEAN DEFAULT FALSE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
    is_valid VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    download_cores INT DEFAULT 1,
    default_save_folder VARCHAR(255),
    default_temp_folder VARCHAR(255),
    delete_after_download BOOLEAN DEFAULT FALSE,
    auto_exit_after_download BOOLEAN DEFAULT FALSE,
    schedule_download BOOLEAN DEFAULT FALSE,
    notify_after_download BOOLEAN DEFAULT FALSE,
    background VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    hotkeys VARCHAR(255)
);
