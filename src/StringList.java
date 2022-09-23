public class StringList implements StringListInterface {

    private class Node{
        String item;
        Node next;

        Node(String item, Node next){
            this.item = item;
            this.next = next;
        }

        Node(String item){
            this(item, null);
        }
    }

    private Node head;

    public StringList(){
        head = null;
    }

    public void insert(String stopWord) {
        //na tsekaroume an aksizei na elegxoume dipla kleidia

        head = new Node(stopWord, head);

    }

    public boolean isEmpty(){
        return head == null;
    }

    // isws anaferoume oti den bre8hke gia na afaire8ei
    public void remove(String stopWord){

        if (isEmpty()){
            System.out.println("The list is empty.");
            return;
        }

        while(head.item.equals(stopWord)) {head = head.next; if (head==null) return;}

        Node prev = head;

        for(Node curr = head.next; curr != null; curr = curr.next){

            if (curr.item.equals(stopWord)){
                prev.next = curr.next;
            }else {
                prev = curr;
            }
        }
    }

    public void printList(){
        for(Node curr = head; curr != null; curr = curr.next){
            System.out.print(curr.item + " ");
        }
        System.out.println();
    }

    // Returns stopwords in format so they can be added to a regex, matched and therefore removed.
    // In example if stopwords are "hello", "wow" then it will return "hello|wow|" (last "|" doesn't matter)
    // it also doesn't matter if a string containing the same stopword more than once is returned.
    public String buildForRegex(){
        if (isEmpty()){return "";}

        StringBuilder s = new StringBuilder();
        for (Node curr = head; curr != null; curr = curr.next){
            s.append(curr.item).append("|");
        }
        return s.toString();
    }

}
