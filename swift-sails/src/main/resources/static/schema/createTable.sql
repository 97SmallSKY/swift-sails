CREATE TABLE IF NOT EXISTS download_segment (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            download_task_id INT,
            segment_number INT,
            segment_name VARCHAR(255),
            segment_path VARCHAR(255),
            create_time TIMESTAMP,
            is_valid VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS download_task (
         id BIGINT AUTO_INCREMENT PRIMARY KEY,
         download_url VARCHAR(255),
         name VARCHAR(255),
         size DECIMAL(19, 2),
         save_directory VARCHAR(255),
         temp_directory VARCHAR(255),
         status VARCHAR(255),
         suffix VARCHAR(255),
         create_time TIMESTAMP,
         is_valid VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS settings (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        download_cores INT,
        default_save_folder VARCHAR(255),
        default_temp_folder VARCHAR(255),
        delete_after_download BOOLEAN,
        auto_exit_after_download BOOLEAN,
        schedule_download BOOLEAN,
        notify_after_download BOOLEAN,
        background VARCHAR(255),
        hotkeys VARCHAR(255),
        is_valid VARCHAR(255),
        create_time TIMESTAMP
);
