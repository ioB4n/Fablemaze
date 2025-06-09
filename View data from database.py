import sqlite3
import pandas as pd
import os


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, 'movie_app.db')

def list_tables(conn):
    """List all table names in the database."""
    query = "SELECT name FROM sqlite_master WHERE type='table';"
    return [row[0] for row in conn.execute(query).fetchall()]

def view_table(conn, table_name, limit=10):
    """Print contents of a table with optional limit."""
    try:
        df = pd.read_sql_query(f"SELECT * FROM {table_name} LIMIT {limit}", conn)
        print(f"\n--- Showing {min(limit, len(df))} rows from '{table_name}' ---")
        print(df)
    except Exception as e:
        print(f"Error reading table {table_name}: {e}")

def main():
    db_path = os.path.abspath(DB_PATH)
    print("Connecting to DB at:", db_path)
    conn = sqlite3.connect(db_path)
    
    print("Connected to database.")
    tables = list_tables(conn)
    if not tables:
        print("No tables found in the database.")
        return

    while True:
        print("\nAvailable tables:")
        for idx, tbl in enumerate(tables):
            print(f"{idx + 1}. {tbl}")
        print("0. Exit")
        
        try:
            choice = int(input("\nSelect a table to view (by number): "))
        except ValueError:
            print("Invalid input.")
            continue

        if choice == 0:
            print("Exiting.")
            break
        elif 1 <= choice <= len(tables):
            table_name = tables[choice - 1]
            try:
                limit = int(input(f"How many rows of '{table_name}' to show? (default 10): ") or 10)
            except ValueError:
                limit = 10
            view_table(conn, table_name, limit)
        else:
            print("Invalid table selection.")
    
    conn.close()

if __name__ == "__main__":
    main()
