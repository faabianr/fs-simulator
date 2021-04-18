$( document ).ready( function() {

    var prompt_id=0;
    var cmdline = 0;
    var responseCmd = 0;

    function init() {
      // Init first command line
         $("#cmdline0").keypress(function (ev) {
           var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                if (keycode == '13') {
                  prompt_id++;
                  cmdline++;
                  executeCommand($("#cmdline0").val());
                  // When the user press enter it creates a new line in prompt
                  setTimeout(
                    function()
                    {
                       writeNewLine();
                    }, 500);
                }
           });
    }

    //Execute every time the user press enter
    function writeNewLine(){
        // Create a clone of the terminal prompt line and put a new id
        $("#terminal_prompt").clone().appendTo("#terminal_body").each(function(){
           $(this).attr("class", "prompt"+(prompt_id));

           var responseLine = $(this).find("#spanResponse p");
           responseCmd++;
           responseLine.attr("id", "responseLine" + responseCmd);
           responseLine.text("");

           var newcmdLine = $(this).find("input");
           newcmdLine.attr("id", "cmdline" + cmdline);
           newcmdLine.val("");
           newcmdLine.focus();
           newcmdLine.keypress(function (ev) {
                var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                 if (keycode == '13') {
                 var commandValue =  newcmdLine.val();
                    prompt_id++;
                    cmdline++;
                    executeCommand(commandValue);
                    setTimeout(
                        function()
                        {
                           writeNewLine();
                        }, 500);
                 }
           });
      });
    }

      function executeCommand(command){
          var commandResponse = "";
          var executionRequest = {
               "input": command
          };
          $.ajax({
                url: 'filesystem/commands',
                dataType: 'json',
                data: JSON.stringify(executionRequest),
                type : 'POST',
                contentType: 'application/json',
                Accept: 'application/json',
                cache : false,
                success: function(resultData) {
                    commandResponse = resultData.output;
                    var pID = "#responseLine" + responseCmd;
                    var response = $(pID).html(commandResponse);
                    $(pID).append(response);
                },
                error: function(errorData){
                   commandResponse = "Invalid Command";
                   var pID = "#responseLine" + responseCmd;
                   $(pID).text(commandResponse);
                 }
          });

      }
  init();
});