#!/bin/bash
# Supply Chain Management Database Setup Script

# Configuration variables - customize these as needed
DB_NAME="scm_db"
DB_USER="scm_user"
DB_PASSWORD="scm_password"
DB_HOST="localhost"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to display script usage
usage() {
    echo -e "${YELLOW}Supply Chain Management Database Setup Script${NC}"
    echo ""
    echo "This script sets up the SCM database with schema and optional sample data."
    echo ""
    echo "Usage:"
    echo "  $0 [options]"
    echo ""
    echo "Options:"
    echo "  -h, --help                   Display this help message"
    echo "  -s, --schema-only            Only create schema (no sample data)"
    echo "  -d, --database-name NAME     Set database name (default: scm_db)"
    echo "  -u, --user USERNAME          Set database username (default: scm_user)"
    echo "  -p, --password PASSWORD      Set database password (default: scm_password)"
    echo "  --host HOST                  Set database host (default: localhost)"
    echo ""
    exit 1
}

# Process command line arguments
LOAD_SAMPLE_DATA=true

while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help)
            usage
            ;;
        -s|--schema-only)
            LOAD_SAMPLE_DATA=false
            shift
            ;;
        -d|--database-name)
            DB_NAME="$2"
            shift 2
            ;;
        -u|--user)
            DB_USER="$2"
            shift 2
            ;;
        -p|--password)
            DB_PASSWORD="$2"
            shift 2
            ;;
        --host)
            DB_HOST="$2"
            shift 2
            ;;
        *)
            echo -e "${RED}Error: Unknown option $1${NC}"
            usage
            ;;
    esac
done

# Check if mysql command is available
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}Error: MySQL client is not installed or not in the PATH${NC}"
    echo "Please install the MySQL client package and try again."
    exit 1
fi

echo -e "${YELLOW}== Supply Chain Management Database Setup ==${NC}"
echo -e "Database: ${GREEN}$DB_NAME${NC}"
echo -e "User: ${GREEN}$DB_USER${NC}"
echo -e "Host: ${GREEN}$DB_HOST${NC}"
echo ""

# Prompt for confirmation
read -p "Continue with these settings? (y/n): " confirm
if [[ $confirm != [yY] && $confirm != [yY][eE][sS] ]]; then
    echo -e "${RED}Setup aborted.${NC}"
    exit 1
fi

# Create database and user
echo -e "\n${YELLOW}Creating database and user...${NC}"
mysql -h "$DB_HOST" -u root -p <<EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$DB_USER'@'$DB_HOST' IDENTIFIED BY '$DB_PASSWORD';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'$DB_HOST';
FLUSH PRIVILEGES;
EOF

if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Failed to create database or user${NC}"
    exit 1
fi

echo -e "${GREEN}Database and user created successfully.${NC}"

# Apply schema
echo -e "\n${YELLOW}Applying database schema...${NC}"
mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < schema.sql

if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Failed to apply database schema${NC}"
    exit 1
fi

echo -e "${GREEN}Schema applied successfully.${NC}"

# Load sample data if requested
if [ "$LOAD_SAMPLE_DATA" = true ]; then
    echo -e "\n${YELLOW}Loading sample data...${NC}"
    mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < sample_data.sql
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Error: Failed to load sample data${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}Sample data loaded successfully.${NC}"
fi

echo -e "\n${GREEN}== Setup completed successfully ==${NC}"
echo -e "The Supply Chain Management database has been set up and is ready to use."
echo -e "Connection string: jdbc:mysql://$DB_HOST:3306/$DB_NAME"
echo -e "\nYou may need to update the persistence.xml file to match these settings." 