import { useEffect, useState } from "react";
import axios from "axios";

function ProfilePage({ readerId }) {

    const [loans, setLoans] = useState([]);
    const [books, setBooks] = useState([]);
    const [bookId, setBookId] = useState("");

  useEffect(() => {
      loadData();
  }, [readerId]);

    const loadData = async () => {

        try {

            const loansRes = await axios.get(
                "http://localhost:8080/loans?page=0&size=100"
            );

            const booksRes = await axios.get(
                "http://localhost:8080/books?page=0&size=100"
            );

            setLoans(loansRes.data);
            setBooks(booksRes.data);

        } catch (error) {
            console.error(error);
        }
    };

    const borrowBook = async () => {

        try {

            const payload = {
                readerId: readerId,
                bookId: Number(bookId),
                issueDate: new Date().toISOString().split("T")[0],
                returnDate: null
            };

            await axios.post(
                "http://localhost:8080/loans",
                payload
            );

            setBookId("");
            loadData();

        } catch (error) {
            console.error(error);
        }
    };

    const getBookTitle = (id) => {

        const book = books.find(b => b.id === id);
        return book ? book.title : "Unknown";
    };

   const myLoans = loans.filter(
       loan => loan.readerId === Number(readerId)
   );

    return (

        <div className="page">

            <h1>My Profile</h1>

            <p>
                Current Reader ID: {readerId}
            </p>

            {/* Borrow section */}
            <div className="search-panel">

                <input
                    placeholder="Book ID"
                    value={bookId}
                    onChange={(e) =>
                        setBookId(e.target.value)
                    }
                />

                <button onClick={borrowBook}>
                    Borrow Book
                </button>

            </div>

            {/* Loans */}
            <h2>My Loans</h2>

            <div className="books-grid">

                {myLoans.map(loan => (

                    <div
                        className="book-card"
                        key={loan.id}
                    >

                        <h3>
                            {getBookTitle(loan.bookId)}
                        </h3>

                        <p>
                            Issue date: {loan.issueDate}
                        </p>

                        <p>
                            Return date: {loan.returnDate || "not returned"}
                        </p>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default ProfilePage;
