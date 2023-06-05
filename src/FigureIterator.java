import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class FigureIterator implements Iterator<Figure> {
    private List<Figure> figures;
    private int currentIndex;

    public FigureIterator(List<Figure> figures) {
        this.figures = figures;
        this.currentIndex = -1;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < figures.size() - 1;
    }

    @Override
    public Figure next() {
        if (hasNext()) {
            currentIndex++;
            return figures.get(currentIndex);
        }
        throw new NoSuchElementException("No more elements in the iterator.");
    }

    @Override
    public void remove() {
        if (currentIndex >= 0 && currentIndex < figures.size()) {
            figures.remove(currentIndex);
            currentIndex--;
        } else {
            throw new IllegalStateException("Invalid state for remove().");
        }
    }
}