@echo off
REM Supply Chain Management Database Setup Script for Windows

REM Configuration variables - customize these as needed
set DB_NAME=scm_db
set DB_USER=scm_user
set DB_PASSWORD=scm_password
set DB_HOST=localhost

REM Batch script parameters
set LOAD_SAMPLE_DATA=1

REM Parse command line arguments
:parse_args
if "%~1"=="" goto continue
if "%~1"=="-h" goto usage
if "%~1"=="--help" goto usage
if "%~1"=="-s" (
    set LOAD_SAMPLE_DATA=0
    shift
    goto parse_args
)
if "%~1"=="--schema-only" (
    set LOAD_SAMPLE_DATA=0
    shift
    goto parse_args
)
if "%~1"=="-d" (
    set DB_NAME=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="--database-name" (
    set DB_NAME=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="-u" (
    set DB_USER=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="--user" (
    set DB_USER=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="-p" (
    set DB_PASSWORD=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="--password" (
    set DB_PASSWORD=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="--host" (
    set DB_HOST=%~2
    shift
    shift
    goto parse_args
)

echo Error: Unknown option %~1
goto usage

:usage
echo.
echo Supply Chain Management Database Setup Script
echo.
echo This script sets up the SCM database with schema and optional sample data.
echo.
echo Usage:
echo   %0 [options]
echo.
echo Options:
echo   -h, --help                   Display this help message
echo   -s, --schema-only            Only create schema (no sample data)
echo   -d, --database-name NAME     Set database name (default: scm_db)
echo   -u, --user USERNAME          Set database username (default: scm_user)
echo   -p, --password PASSWORD      Set database password (default: scm_password)
echo   --host HOST                  Set database host (default: localhost)
echo.
exit /b 1

:continue
REM Check if mysql command is available
mysql --version > nul 2>&1
if errorlevel 1 (
    echo Error: MySQL client is not installed or not in the PATH
    echo Please install the MySQL client package and make sure it's in your PATH.
    exit /b 1
)

echo == Supply Chain Management Database Setup ==
echo Database: %DB_NAME%
echo User: %DB_USER%
echo Host: %DB_HOST%
echo.

REM Prompt for confirmation
set /p confirm="Continue with these settings? (y/n): "
if /i "%confirm%" neq "y" if /i "%confirm%" neq "yes" (
    echo Setup aborted.
    exit /b 1
)

REM Create database and user
echo.
echo Creating database and user...
echo CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; > temp_setup.sql
echo CREATE USER IF NOT EXISTS '%DB_USER%'@'%DB_HOST%' IDENTIFIED BY '%DB_PASSWORD%'; >> temp_setup.sql
echo GRANT ALL PRIVILEGES ON %DB_NAME%.* TO '%DB_USER%'@'%DB_HOST%'; >> temp_setup.sql
echo FLUSH PRIVILEGES; >> temp_setup.sql

mysql -h %DB_HOST% -u root -p < temp_setup.sql
if errorlevel 1 (
    echo Error: Failed to create database or user
    del temp_setup.sql
    exit /b 1
)
del temp_setup.sql

echo Database and user created successfully.

REM Apply schema
echo.
echo Applying database schema...
mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < schema.sql
if errorlevel 1 (
    echo Error: Failed to apply database schema
    exit /b 1
)

echo Schema applied successfully.

REM Load sample data if requested
if "%LOAD_SAMPLE_DATA%"=="1" (
    echo.
    echo Loading sample data...
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < sample_data.sql
    
    if errorlevel 1 (
        echo Error: Failed to load sample data
        exit /b 1
    )
    
    echo Sample data loaded successfully.
)

echo.
echo == Setup completed successfully ==
echo The Supply Chain Management database has been set up and is ready to use.
echo Connection string: jdbc:mysql://%DB_HOST%:3306/%DB_NAME%
echo.
echo You may need to update the persistence.xml file to match these settings.

exit /b 0 