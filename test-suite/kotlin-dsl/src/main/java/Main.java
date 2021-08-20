import io.javalin.Javalin;

public class Main {

  public static void main(String[] args)
      throws Exception {
    final Javalin app = Javalin.create().start(7000);
    app.get("/", ctx -> ctx.result("Hello, world!"));
  }
}
